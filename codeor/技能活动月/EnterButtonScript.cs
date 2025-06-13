using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

/// <summary>
/// �������������ť�л�����
/// </summary>
public class EnterButtonScript : MonoBehaviour
{
    public static Action CutDraw;

    // Start is called before the first frame update
    private void Start()
    {
        var but = gameObject.GetComponent<Button>();
        but.onClick.AddListener(ToCream);
    }

    private void ToCream()
    {
        var camera = Camera.main; //��ǰ���

        Debug.Log("�����л���ť������ˣ�");
        SceneManager.LoadScene("Scene01");
        //var curentObjects = gameObject.scene.GetRootGameObjects();
        //var targetObject = curentObjects.FirstOrDefault(f => f.name == "cameraPlane");
        //if(targetObject == null){
        //    var gameObj = GameObject.CreatePrimitive(PrimitiveType.Plane);
        //    gameObj.transform.position = camera.transform.position;
        //    gameObj.transform.rotation *= Quaternion.Euler(90f,0f,90f);
        //    gameObj.transform.localScale = Vector3.one * 100f;
        //    gameObj.name = "cameraPlane";
        //    camera.transform.position -= new Vector3(1, 1, 1);
        //}

    }
}
