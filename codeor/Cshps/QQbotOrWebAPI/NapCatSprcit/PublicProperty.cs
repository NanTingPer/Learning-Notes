using NapCatSprcit.MessagesService;
using NapCatSprcit.WebSocketConnection;

namespace NapCatSprcit
{
    /// <summary>
    /// 公共属性
    /// <para> 包括 wsURI HttpURL </para>
    /// </summary>
    public class PublicProperty
    {
        public static string WebSocketURI = "";
        public static string HttpURI = "";
        public static Messages? Messages;
        public static Connection? Connection;
    }

}
