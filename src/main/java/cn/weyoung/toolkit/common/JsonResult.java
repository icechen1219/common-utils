///:JsonResult.java
package cn.weyoung.toolkit.common;

import java.io.Serializable;

/**
 * @author icechen1219
 * @date 2019/02/04
 */
public class JsonResult implements Serializable {
    private String sessionId;
    private Integer errorCode;
    private String errorMsg;
    private Object data;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "sessionId='" + sessionId + '\'' +
                ", errorCode=" + errorCode +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }
}
///:JsonResult.java
