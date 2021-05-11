package cn.jpps.security.util;

/**
 * @ClassName: RespBean
 * @Author: jiatp
 * @Date: 2020/12/2 14:13
 * @Description: t
 */
public class RespBean {
    // 响应业务状态
    private Integer status;
    // 响应消息
    private String msg;
    // 响应中的数据
    private Object data;
    private String ok;    // 不使用


    public static RespBean build(Integer status, String msg, Object data) {
        return new RespBean(status, msg, data);
    }


    public static RespBean ok(Object data) {
        return new RespBean(data);
    }


    public static RespBean ok() {
        return new RespBean(null);
    }

    public static RespBean ok(String msg,Object obj) {
        return  new RespBean(200, msg, obj);
    }

    public static RespBean errorMsg(String msg) {
        return new RespBean(500, msg, null);
    }

    public static RespBean errorMap(Object data) {
        return new RespBean(501, "error", data);
    }

    public static RespBean errorTokenMsg(String msg) {
        return new RespBean(502, msg, null);
    }

    public static RespBean errorException(String msg) {
        return new RespBean(555, msg, null);
    }


    public RespBean() {
    }


    public RespBean(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    public RespBean(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }


    public Boolean isOK() {
        return this.status == 200;
    }


    public Integer getStatus() {
        return status;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }


    public String getMsg() {
        return msg;
    }


    public void setMsg(String msg) {
        this.msg = msg;
    }


    public Object getData() {
        return data;
    }


    public void setData(Object data) {
        this.data = data;
    }


    public String getOk() {
        return ok;
    }


    public void setOk(String ok) {
        this.ok = ok;
    }

}
