package WaterSensors;

import java.util.Objects;

public class WaterSensor
{
    /**
     * 水位传感器类型
     */
    public String id;

    /**
     * 传感器记录时间戳
     */
    public Long ts;

    /**
     * 水位记录
     */
    public int vc;

    /**
     * 空参 一定要提供
     */
    public WaterSensor()
    {
    }

    /**
     * 构建一个水位传感器
     * @param id 水位传感器类型
     * @param ts 传感器记录时间戳
     * @param vc 水位记录
     */
    public WaterSensor(String id, Long ts, int vc)
    {
        this.id = id;
        this.ts = ts;
        this.vc = vc;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Long getTs()
    {
        return ts;
    }

    public void setTs(Long ts)
    {
        this.ts = ts;
    }

    public int getVc()
    {
        return vc;
    }

    public void setVc(int vc)
    {
        this.vc = vc;
    }

    @Override
    public String toString()
    {
        return "WaterSensor{" +
            "id='" + id + '\'' +
            ", ts=" + ts +
            ", vc=" + vc +
            '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaterSensor that = (WaterSensor) o;
        return vc == that.vc && Objects.equals(id, that.id) && Objects.equals(ts, that.ts);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, ts, vc);
    }
}
