using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using TMPro;
using UnityEditor;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.SceneManagement;

/// <summary>
/// <para> 关于点击 </para>
/// <para> 1. 为相机添加射线检测 (类似生成一个空带碰撞的物体朝鼠标点击方向移动) </para>
/// <para> 2. 添加EventSystem </para>
/// <para> 3. 设置obj的碰撞体 </para>
/// </summary>
public class FemaleA : MonoBehaviour, IPointerClickHandler
{
    private bool _isMsg = false;
    private GameObject _msgCanvas;
    private GameObject _toSceneCanvas;

    //改用字典或许更好 
    //Dictionary<string, List<GameObject>>
    private readonly List<GameObject> _toSceneCanvasObjects = new();
    private readonly List<GameObject> _msgCanvasObjects = new();
    private readonly List<Func<bool>> _expUpdates = new();
    private readonly List<Func<bool>> _lastExpUpdate = new();
    private readonly List<Coroutine> _coroutineList = new();
    private CameraModel _cameraModel = CameraModel.Player;


    public void OnPointerClick(PointerEventData eventData)
    {
        if (_msgCanvas.activeSelf == true)
            return;
        _isMsg = true;
        _msgCanvas.SetActive(true);
        _expUpdates.Add(ConvAlp);
    }

    private bool ConvAlp()
    {
        var panel = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgPanel");
        var canvasg = panel.GetComponent<CanvasGroup>();
        canvasg.alpha += 0.01f;
        if (canvasg.alpha >= 1)
        {
            _lastExpUpdate.Add(MsgText);
            return true;
        }
        else
        {
            return false;
        }
    }

    private bool MsgText()
    {
        #region del
        //if ((int)Time.time % 5 == 0)
        //{
        //    GameObject game = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgText");
        //    var textmpro = game.GetComponent<TextMeshProUGUI>();//TextMeshProUGUI
        //    int curLength;
        //    if (textmpro.text is null)
        //    {
        //        curLength = 0;
        //    }
        //    else
        //    {
        //        curLength = textmpro.text.Length;
        //    }

        //    if (curLength >= msg.Length)
        //    {
        //        return true;
        //    }
        //    textmpro.text += msg[curLength];
        //}
        #endregion
        _coroutineList.Add(StartCoroutine(GetMsg()));
        return true;
    }

    private void Awake()
    {
        LoadMustSource();
    }

    private void Start()
    {
        _msgCanvas.SetActive(false);
        _toSceneCanvasObjects.ForEach(f => f.SetActive(false));
        _toSceneCanvas.SetActive(false);
        //设置相机角度
        Camera.main.transform.rotation *= Quaternion.Euler(30, 0, 0);
    }

    private void OnTriggerEnter(Collider other)
    {
        if(other.gameObject.name == "DoorCol")
        {
            _cameraModel = CameraModel.Foor;
        }
        if(other.gameObject.name == "Door")
        {
            _lastExpUpdate.Add(ToScene);
        }
    }

    private bool ToScene()
    {
        foreach (var item in _toSceneCanvasObjects)
        {
            var cg = item.GetComponent<CanvasGroup>();
            if (cg == null)
            {
                item.AddComponent<CanvasGroup>();
            }
        }

        _coroutineList.Add(StartCoroutine(SubAHP()));
        return true;
    }

    IEnumerator SubAHP()
    {
        var witheScreenCG = _toSceneCanvasObjects.FirstOrDefault(f => f.name == "WitheScreen").GetComponent<CanvasGroup>();
        var textBoxCG = _toSceneCanvasObjects.FirstOrDefault(f => f.name == "ReText").GetComponent<CanvasGroup>();
        witheScreenCG.alpha = 0f;
        textBoxCG.alpha = 0f;
        _toSceneCanvas.SetActive(true);
        witheScreenCG.gameObject.SetActive(true);
        _isMsg = true;
        while (textBoxCG.alpha < 1f)
        {
            if(witheScreenCG.alpha < 1f)
            {
                witheScreenCG.alpha = Vector3.Lerp(new Vector3(witheScreenCG.alpha, 0, 0), new Vector3(1.2f, 0, 0), 0.04f).x;
            }
            else
            {
                textBoxCG.gameObject.SetActive(true);
                textBoxCG.alpha = Vector3.Lerp(new Vector3(textBoxCG.alpha, 0, 0), new Vector3(1.2f, 0, 0), 0.03f).x;
            }

            yield return new WaitForSeconds(.03f);
        }
        SceneManager.LoadScene("Scene00");
        ExitMsg();
        yield break;
    }

    private void OnTriggerExit(Collider other)
    {
        if (other.gameObject.name == "DoorCol")
        {
            _cameraModel = CameraModel.Player;
        }
    }

    /// <summary>
    /// 加载必要资源
    /// </summary>
    private void LoadMustSource()
    {
        var objs = gameObject.scene.GetRootGameObjects();
        _msgCanvas = objs.FirstOrDefault(f => f.name == "MsgCanvas");
        _toSceneCanvas = objs.FirstOrDefault(f => f.name == "ToScene");
        foreach (var item in _msgCanvas.transform)
        {
            _msgCanvasObjects.Add((item as Component).gameObject);
        }
        foreach (var item in _toSceneCanvas.transform)
        {
            _toSceneCanvasObjects.Add((item as Component).gameObject);
        }

        #region 初始化 对话框
        var msgPanel = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgPanel");
        var rectTransform = msgPanel.GetComponent<RectTransform>();
        //rectTransform.anchorMin = new Vector2(0.5f, 0);
        //rectTransform.anchorMax = new Vector2(0.5f, 0);
        //rectTransform.pivot = new Vector2(0.5f, 0);
        //rectTransform.localScale = new Vector3(2f, 1f, 1f);

        var canvas = msgPanel.AddComponent<CanvasGroup>();
        canvas.alpha = 0f;
        #endregion
    }

    private List<Func<bool>> dellist = new ();
    public object lockObject = new object();
    private void Update()
    {
        //按下任意键退出对话
        if (Input.anyKeyDown)
        {
            ExitMsg();
        }

        if(_isMsg == false)
        {
            #region 移动
            var very = new Vector3(0, 0, 0);
            //A
            if (Input.GetKey(KeyCode.A))
            {
                very += Vector3.left;
            }

            if (Input.GetKey(KeyCode.D))
            {
                very += Vector3.right;
            }

            if (Input.GetKey(KeyCode.W))
            {
                very += new Vector3(0, 0, 1);
            }

            if (Input.GetKey(KeyCode.S))
            {
                very += new Vector3(0, 0, -1);
            }

            //001 010
            float angle = Vector3.SignedAngle(Vector3.forward, very, Vector3.up);

            var obj = gameObject;
            var rot = Quaternion.Lerp(obj.transform.rotation, Quaternion.Euler(0, angle, 0), 0.03f);

            obj.transform.rotation = rot;

            very *= 0.1f;
            obj.transform.position += very;
            #endregion
        }
        ExpUpdate();
        CameraPosition();
    }
    
    /// <summary>
    /// 相机位置更新
    /// </summary>
    private void CameraPosition()
    {
        var obj = gameObject;
        var camera = Camera.main;
        switch (_cameraModel)
        {
            case CameraModel.Player:
                //camera.transform.rotation = 
                camera.transform.rotation = Quaternion.Lerp(camera.transform.rotation, Quaternion.Euler(30, 0, 0), 0.02f);
                camera.transform.position = Vector3.Lerp(camera.transform.position, obj.transform.position + new Vector3(2f, 4f, -5f), 0.02f);
                break;
            case CameraModel.Foor:
                camera.transform.rotation = Quaternion.Lerp(camera.transform.rotation, Quaternion.Euler(20, 50, 20), 0.02f);
                camera.transform.position = Vector3.Lerp(camera.transform.position, obj.transform.position + new Vector3(-5f, 15f, -15f), 0.02f);
                break;
        }
        
    }


    /// <summary>
    /// 额外方法调用
    /// </summary>
    private void ExpUpdate()
    {
        lock (lockObject)
        {
            foreach (var func in _expUpdates)
            {
                if (func.Invoke())
                {
                    dellist.Add(func);
                }
            }
            _expUpdates.AddRange(_lastExpUpdate);
            _lastExpUpdate.Clear();
            _expUpdates.RemoveAll(f =>
            {
                return dellist.Contains(f);
            });
            dellist.Clear();
        }
    }

    /// <summary>
    /// 对话文本
    /// </summary>
    private IEnumerator GetMsg()
    {
        string msg = "Hello! Unity!";
        for (int i = 0; i < msg.Length; i++)
        {
            GameObject game = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgText");
            var textmpro = game.GetComponent<TextMeshProUGUI>();//TextMeshProUGUI
            textmpro.text += msg[i];
            yield return new WaitForSeconds(.2f);
        }
        yield break;
    }

    /// <summary>
    /// 退出对话，并初始化面板设置，恢复移动
    /// </summary>
    private void ExitMsg()
    {
        if(_msgCanvas.activeSelf == false)
        {
            return;
        }

        _coroutineList.ForEach(f => StopCoroutine(f));
        GameObject game = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgText");
        var textmpro = game.GetComponent<TextMeshProUGUI>();//TextMeshProUGUI
        textmpro.text = "";

        var panel = _msgCanvasObjects.FirstOrDefault(f => f.name == "MsgPanel");
        var canvasg = panel.GetComponent<CanvasGroup>();
        canvasg.alpha = 0f;

        _msgCanvas.SetActive(false);

        _expUpdates.Clear();
        _lastExpUpdate.Clear();
        _coroutineList.Clear();

        _isMsg = false;
        return;
    }

    public enum CameraModel
    {
        Player,
        Foor
    }
}
