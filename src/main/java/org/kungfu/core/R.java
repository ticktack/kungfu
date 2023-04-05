package org.kungfu.core;

import com.jfinal.json.Json;
import com.jfinal.kit.Func;
import com.jfinal.kit.StrKit;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap {
    private static final long serialVersionUID = -2356729333387895526L;
    static String STATE = "code";
    static Object STATE_OK = 200;
    static Object STATE_FAIL = 600; // 自定义响应码
    static Func.F30<R, String, Object> stateWatcher = null;
    static Func.F21<Boolean, Object, Boolean> okFailHandler = null;
    static String DATA = "data";
    static boolean dataWithOkState = false;
    static String MSG = "msg";

    static String SUCCESS = "ok";

    static String FAIL = "fail";

    public R() {
    }

    public static R of(Object key, Object value) {
        return (new R()).set(key, value);
    }

    public static R by(Object key, Object value) {
        return (new R()).set(key, value);
    }

    public static R create() {
        return new R();
    }

    public static R ok() {
        return (new R()).setOk();
    }

    public static R ok(String msg) {
        return (new R()).setOk()._setMsg(msg);
    }

    public static R ok(Object key, Object value) {
        return (new R()).setOk().set(key, value);
    }

    public static R fail() {
        return (new R()).setFail();
    }

    public static R fail(Object code) {
        return (new R()).setFail(code)._setMsg(FAIL);
    }
    public static R fail(Object code, String msg) {
        return (new R()).setFail(code)._setMsg(msg);
    }

    public static R fail(String msg) {
        return (new R()).setFail()._setMsg(msg);
    }

    /** @deprecated */
    @Deprecated
    public static R fail(Object key, Object value) {
        return (new R()).setFail().set(key, value);
    }

    public static R state(Object value) {
        return (new R())._setState(value);
    }

    public static R data(Object data) {
        return (new R())._setData(data);
    }

    public static R msg(String msg) {
        return (new R())._setMsg(msg);
    }

    protected R _setState(Object value) {
        super.put(STATE, value);
        if (stateWatcher != null) {
            stateWatcher.call(this, STATE, value);
        }

        return this;
    }

    protected R _setData(Object data) {
        super.put(DATA, data);
        if (dataWithOkState) {
            this._setState(STATE_OK);
        }

        return this;
    }

    protected R _setMsg(String msg) {
        super.put(MSG, msg);
        return this;
    }

    public R setOk() {
        super.put(MSG, SUCCESS);
        return this._setState(STATE_OK);
    }

    public R setFail() {
        super.put(MSG, FAIL);
        return this._setState(STATE_FAIL);
    }

    public R setFail(Object code) {
        return this._setState(code);
    }

    public boolean isOk() {
        Object state = this.get(STATE);
        if (STATE_OK.equals(state)) {
            return true;
        } else if (!STATE_OK.equals(state)) {
            return false;
        } else if (okFailHandler != null) {
            return (Boolean)okFailHandler.call(Boolean.TRUE, state);
        } else {
            throw new IllegalStateException("调用 isOk() 之前，必须先调用 ok()、fail() 或者 setOk()、setFail() 方法");
        }
    }

    public boolean isFail() {
        Object state = this.get(STATE);
        if (!STATE_OK.equals(state)) {
            return true;
        } else if (STATE_OK.equals(state)) {
            return false;
        } else if (okFailHandler != null) {
            return (Boolean)okFailHandler.call(Boolean.FALSE, state);
        } else {
            throw new IllegalStateException("调用 isFail() 之前，必须先调用 ok()、fail() 或者 setOk()、setFail() 方法");
        }
    }

    public R set(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public R setIfNotBlank(Object key, String value) {
        if (StrKit.notBlank(value)) {
            this.set(key, value);
        }

        return this;
    }

    public R setIfNotNull(Object key, Object value) {
        if (value != null) {
            this.set(key, value);
        }

        return this;
    }

    public R set(Map map) {
        super.putAll(map);
        return this;
    }

    public R set(R ret) {
        super.putAll(ret);
        return this;
    }

    public R delete(Object key) {
        super.remove(key);
        return this;
    }

    public <T> Object getAs(Object key) {
        return this.get(key);
    }

    public <T> T getAs(Object key, T defaultValue) {
        Object ret = this.get(key);
        return ret != null ? (T) ret : defaultValue;
    }

    public String getStr(Object key) {
        Object s = this.get(key);
        return s != null ? s.toString() : null;
    }

    public Integer getInt(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.intValue() : null;
    }

    public Long getLong(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.longValue() : null;
    }

    public Double getDouble(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.doubleValue() : null;
    }

    public Float getFloat(Object key) {
        Number n = (Number)this.get(key);
        return n != null ? n.floatValue() : null;
    }

    public Number getNumber(Object key) {
        return (Number)this.get(key);
    }

    public Boolean getBoolean(Object key) {
        return (Boolean)this.get(key);
    }

    public boolean notNull(Object key) {
        return this.get(key) != null;
    }

    public boolean isNull(Object key) {
        return this.get(key) == null;
    }

    public boolean isTrue(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && (Boolean)value;
    }

    public boolean isFalse(Object key) {
        Object value = this.get(key);
        return value instanceof Boolean && !(Boolean)value;
    }

    public String toJson() {
        return Json.getJson().toJson(this);
    }

    public boolean equals(Object ret) {
        return ret instanceof R && super.equals(ret);
    }
}
