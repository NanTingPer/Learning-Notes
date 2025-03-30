using Humanizer;
using Microsoft.Xna.Framework.Graphics;
using ReLogic.Content;
using System;
using System.Linq;
using Terraria.GameContent.UI.Elements;
using Terraria.ModLoader;
using Terraria.UI;

namespace InfiniteL.UI;

public class BossButton : UIImageButton
{
    public static List<BossButton> Buttons = [];
    private Asset<Texture2D> _texture;

    private int count = 0;
    private int leftCount = 0;
    public bool Is = false;
    public string Name { get; set; }
    private float initTop;
    private float initLeft;
    private float currTop;
    private float currLeft;
    public BossButton(Asset<Texture2D> texture, string name) : base(texture)
    {
        _texture = texture;
        Name = name;
        count = Buttons.Count - ((Buttons.Count / 5) * 5);
        leftCount = Buttons.Count / 5;
        initTop = 50 * leftCount;
        initLeft = 50 * count;
        
        Top.Set(initTop, 0);
        Left.Set(initLeft, 0);


        Width.Set(50, 0);
        Height.Set(50, 0);
        
        Buttons.Add(this);
        OnLeftClick += new MouseEvent(AlftIs);
    }

    private void AlftIs(UIMouseEvent evt, UIElement listeningElement)
    {
        foreach (var item1 in Buttons) {
            if(item1 != this) item1.Is = false;
        }
        Is = !Is;
    }


    public override void Draw(SpriteBatch spriteBatch)
    {
        CalculatedStyle cs = GetDimensions();
        Vector2 pos = new Vector2(cs.X, cs.Y);
        spriteBatch.Draw(_texture.Value, pos, Color.White * (Is ? 1f : Alahp));

        #region 血的教训？
        /*
        float top = 0, left = 0;
        //Parent //父组建
        UIElement parent = Parent;
        while(parent != null) {
            top += parent.Top.Pixels + parent.MarginTop;    //父组建 top与额外距
            left += parent.Left.Pixels + parent.MarginLeft; //父组建 left与额外距
            parent = parent.Parent;
        }
        currTop = top += initTop + MarginTop;      //最终绘制的Top位置
        currLeft = left += initLeft + MarginLeft;   //最终绘制的Left位置
        spriteBatch.Draw(_texture.Value, new Vector2(left, top), Color.White * (Is ? 1f : Alahp));
        */
        #endregion
        //base.Draw(spriteBatch);
    }
    #region 透明度效果 点击位置调整
    private Action Action;
    public float Alahp = 0.5f;
    public override void Update(GameTime gameTime)
    {
        Action?.Invoke();
        base.Update(gameTime);
    }
    public override void MouseOver(UIMouseEvent evt)
    {
        Action -= LittleSub;
        Action += LittleAdd;
        base.MouseOver(evt);
    }
    public override void MouseOut(UIMouseEvent evt)
    {
        Action -= LittleAdd;
        Action += LittleSub;
    }

    private void LittleAdd()
    {
        if (Alahp <= 1) Alahp += 0.2f;
        if (Alahp >= 1) Action -= LittleAdd;
    }

    private void LittleSub()
    {
        if (Alahp >= 0.5) Alahp -= 0.2f;
        if (Alahp <= 0.5) Action -= LittleSub;
    }
    #endregion
}