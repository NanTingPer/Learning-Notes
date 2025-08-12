//https://github.com/xoofx/markdig?tab=readme-ov-file
//https://www.nuget.org/packages/Markdig.Signed/
using System.Text;

string? markdown = File.ReadAllText("D:\\CodeRun\\Learning-Notes\\C#MD\\JWT.md");
string markdownHTML = Markdig.Markdown.ToHtml(markdown);
markdownHTML += """

    <style>
    *:not(span) {
        color: aliceblue;
        background-color: #414141;
    }
    pre:has(> code) {
        background-color:#484848;
        padding: 1em;
        border-radius: 4px;
        overflow: auto; 
        scrollbar-width: none;
    }

    pre:has(> code) code {
        background-color: transparent;
        color: aliceblue;
        scrollbar-width: none;
    }

    p:has(> code) code {
        font-size: 5px;
        color:blanchedalmond;
    }

    blockquote:has(> p) p {
        border-left: 3px solid #242323;
        padding-left: 4px;
    }
    </style>
    """;

StringBuilder sb = new StringBuilder();
sb.Append("""
    <head>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-okaidia.min.css" rel="stylesheet" />
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-csharp.min.js"></script>
    </head>
    """)
    .Append(markdownHTML);
File.WriteAllText("D:\\CodeRun\\Learning-Notes\\C#MD\\JWT.html", sb.ToString());