# FNA生成程序式音频

```cs
using System;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;

public class SimpleSineWave : Game
{
    private DynamicSoundEffectInstance _sound;
    
    protected override void Initialize()
    {
        // 1. 创建动态音频实例（44.1kHz，单声道）
        _sound = new DynamicSoundEffectInstance(44100, AudioChannels.Mono);
        
        // 2. 生成C4正弦波数据（261.63Hz，0.5秒）
        byte[] audioData = GenerateC4SineWave(0.5f);
        
        // 3. 提交数据并播放
        _sound.SubmitBuffer(audioData);
        _sound.Play();
        
        base.Initialize();
    }
    
    private byte[] GenerateC4SineWave(float durationSeconds)
    {
        float frequency = 261.63f;    // C4频率
        float amplitude = 0.3f;       // 音量30%
        int sampleRate = 44100;       // 采样率
        int totalSamples = (int)(sampleRate * durationSeconds);
        
        byte[] pcmData = new byte[totalSamples * 2];  // 16位 = 2字节/采样
        double angleIncrement = 2.0 * Math.PI * frequency / sampleRate;
        double currentAngle = 0;
        
        for (int i = 0; i < totalSamples; i++)
        {
            // 生成正弦波样本
            short sampleValue = (short)(Math.Sin(currentAngle) * amplitude * 32767);
            
            // 小端序存储
            pcmData[i * 2] = (byte)(sampleValue & 0xFF);
            pcmData[i * 2 + 1] = (byte)((sampleValue >> 8) & 0xFF);
            
            currentAngle += angleIncrement;
        }
        
        return pcmData;
    }
    
    protected override void Dispose(bool disposing)
    {
        _sound?.Dispose();
        base.Dispose(disposing);
    }
}
```

# 使用内置SoundEffect

```cs
// 使用FNA的简化方法
public static SoundEffect CreateC4Tone(float duration)
{
    float frequency = 261.63f;
    int sampleCount = (int)(44100 * duration);
    float[] samples = new float[sampleCount];
    
    for (int i = 0; i < sampleCount; i++)
    {
        samples[i] = 0.3f * (float)Math.Sin(2 * Math.PI * frequency * i / 44100);
    }
    
    return new SoundEffect(samples, 44100, AudioChannels.Mono);
}
// 使用：CreateC4Tone(1.0f).Play();
```

# 不用位运算

```cs
public static byte[] GenerateC4SineWaveEasy(float durationSeconds)
{
    const float frequency = 261.63f;  // C4
    const float amplitude = 0.3f;
    const int sampleRate = 44100;
    
    int totalSamples = (int)(sampleRate * durationSeconds);
    byte[] pcmData = new byte[totalSamples * 2];
    
    // 角度增量
    double angleStep = 2.0 * Math.PI * frequency / sampleRate;
    
    for (int i = 0; i < totalSamples; i++)
    {
        // 1. 计算正弦值
        double sine = Math.Sin(angleStep * i);
        
        // 2. 应用振幅
        double sample = sine * amplitude;
        
        // 3. 转换为16位整数（方法A：使用BitConverter）
        short intValue = (short)(sample * 32767);
        byte[] twoBytes = BitConverter.GetBytes(intValue);
        
        // 4. 存储到PCM数组
        pcmData[i * 2] = twoBytes[0];
        pcmData[i * 2 + 1] = twoBytes[1];
    }
    
    return pcmData;
}
```

