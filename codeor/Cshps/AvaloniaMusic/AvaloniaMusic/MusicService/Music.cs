using System;
using System.Diagnostics;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace AvaloniaMusic.MusicService;

public static class Music
{
    public static async void GetMusic(this object obj, string keyWord)
    {
        var hc = new HttpClient();
        string json = JsonSerializer.Serialize(new SearchMusicJson { KeyWords = keyWord, Limit = 10 }); 
        var content = new StringContent(json, Encoding.UTF8, "application/json");
        //hc.PostAsync("https://music.163.com/search", content);
        var result = await hc.GetAsync("https://music.163.com/#/search/m/?" + json);
        var value = await result.Content!.ReadAsStringAsync();
        Debug.WriteLine(value);
    }
}

[Serializable]
public class SearchMusicJson
{
    /// <summary>
    /// 搜索关键字
    /// </summary>
    [JsonPropertyName("keywords")]
    public string KeyWords { get; set; }

    /// <summary>
    /// 单页数量
    /// </summary>
    [JsonPropertyName("limit")]
    public int Limit { get; set; }

    /// <summary>
    /// 第多少页
    /// </summary>
    [JsonPropertyName("offset")]
    public int Offset { get; set; }

    [JsonPropertyName("type")]
    public int Type { get; set; } = 1;

    [JsonPropertyName("cookie")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Cookie { get; set; } =
        "csrf_token=NMTID=00OX43kXygbzUu3aUDMv4_z_azWjQ4AAAGT8W8tKA; _iuqxldmzr_=32; WEVNSM=1.0.0; WNMCID=lsjwsi.1734922416814.01.0; WM_TID=0XZuDuMx7qxERQVVBFPEk6bTKzhouwJV; NTES_P_UTID=MB3ysVDZ51s3uhVkukHkbFAIxgUiDilA|1745242254; P_INFO=a3380991070@163.com|1745242254|0|mail163|00&99|null&null&null#CN&null#10#0#0|&0||a3380991070@163.com; nts_mail_user=a3380991070@163.com:-1:1; __snaker__id=K7pwS2liZMQ0bcV2; usertrack=CsQtbWgLh0YfeWBz7ZtHAg==; _ntes_nnid=0feadbc426b7e2025ff74a0b79a726a0,1745766169394; _ntes_nuid=0feadbc426b7e2025ff74a0b79a726a0; WM_NI=SlvMulSivCOdIaWplEwxx16i%2F%2BlvAxI1yiePWXBNSr9U1KpsRkAQXLr4A67JJHU1V%2BkXQu4tA1iNirwlMe2rseDp%2B5QCLNm6pfYZ28F4ru%2Bgl64mpydNjoGfGWpYUrbbQXg%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6ee8ded4898b09e93fb5489b48fa7d45e879e8fb1db5c8fb9a98aae49f7bcbd84f12af0fea7c3b92a91b4baaef86891b7baa4ce708bbf9991ef4fb5a8b8d2b563edf5a497c4748befb78ae85c95888eafd27afb8bffacf37992b58fb2db48b2ec8fd7b6428a989bd2d521a9889f96b78083efa7afec7ab5b48386b17ab1bb8c91aa3f939d84aff03eb4ba97bbb13486ecbe96aa6db0eafc8ef373a892a690c66e8589c0bad87aa2be9fb9e637e2a3; JSESSIONID-WYYY=zmBAjmfz5ztnXx%2FcXAg7S6QnvIpuo%2BEogz0EY2lTnBI7lVzWGl4zJmjMgbbW57VHGQAUKqszSeVCMFrSyAfOU4%2FlCmIs44SHONF31aK25T6hjMjErQGWG%2BtHp02QsqX7Cn1ulU%5CZIBIRzQNDqYCZdOrmMV6xPu9zbvB1IzxHSOfbu2t0%3A1746525708103; gdxidpyhxdE=TfiCv%2FrfSVx38CCLBW%2BIqT%2F%5CYAKKe%5CbG7YBvY8iCRHTK%5C1stSIUH8PriZ2B%2FKX6LUW%2F%5C4z3H8GQzq6x8t9dYQ2Ey9Knq7pgGvW6Jn9S6keOtiBqVg80P1Ne7Lv79Y%5CxP8kwNRKTblVSGNAS%2FXo9d6qLDoaKAgDE8Wuu7uMip%2Brz3hhe%2F%3A1746524839676; MUSIC_U=00C6C365E9B17AAE5814F8DF2367FBEA2D223F45D3BFE6BA110589ACE28A36CD6E5BA2058B80288D51FD1ABCADBFC2D7A5DFD2EC9765B2F1B0C632F31B7DF616503CEA6C7D36EA917D9A153795738837B19923F8739F1824893E484F74236095E1FF4E9328FD5EFAB8E341E44DBEEB43F212EB723DCB0812FFC4F037D96227A7F47A263D06E3B83D37D3BDF0E94B7692205600BD48ADD3F24E1EBC4B10E641DCCD0153E6204A07B037B556AAA2E7A527BC89DF3FE4575EF98910D12E11106D2D1A5C9AB18AD1C4D7CFC2880B8765C7522CE64F5DDE5DC127E58E892E34ED9B356B975515B4ACCC929B54C601C45CA0B89B505CA389EAC78432F9E79AFEF99EE6C11C5F825A1D14E2AA51D711AAC2891BB60A07612BA05CD80F9105947B276AED5B71C911DB1BDD33067FED0B8D8829B6A99ECF3D358F89410EE7F70966270C2321; __csrf=14e5bac76ad64da1083e00bded56adb3; __remember_me=true";

    [JsonPropertyName("timestamp")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
    public string? Timestamp { get; set; }
}