package me.ahaliulang.testdemo.mvp.net;

/**
 * model基类 ，后台返回参数的固定模型
 * Created by lgh on 2018/2/2.
 */
public class BaseModel<T>
{
	private int code;
	private String message;
	private Data<T> data;

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Data<T> getData()
	{
		return data;
	}

	public void setData(Data<T> data)
	{
		this.data = data;
	}

	public static class Data<T>
	{
		private Status status;
		private T result;

		public Status getStatus()
		{
			return status;
		}

		public void setStatus(Status status)
		{
			this.status = status;
		}

		public T getResult()
		{
			return result;
		}

		public void setResult(T result)
		{
			this.result = result;
		}
	}

	public static class Status
	{
		private int code;
		private String msg;
		private Dynamic dynamic;

		public int getCode()
		{
			return code;
		}

		public void setCode(int code)
		{
			this.code = code;
		}

		public String getMsg()
		{
			return msg;
		}

		public void setMsg(String message)
		{
			this.msg = message;
		}

		public Dynamic getDynamic()
		{
			return dynamic;
		}

		public void setDynamic(Dynamic dynamic)
		{
			this.dynamic = dynamic;
		}
	}

	public static class Dynamic
	{
		private Attach attach;

		public Attach getAttach()
		{
			return attach;
		}

		public void setAttach(Attach attach)
		{
			this.attach = attach;
		}
	}

	public static class Attach{
		private String uri_of_auth_deny;// 权限受限跳转地址  //在权限受限的时候才会出现 "uri_of_auth_deny": "sns://goto?type=inner_app&pid=1280221&step=1&cstep=3&upicon_success_after_reload_auth=1",  pid=>页面id,step=>当前步数,cstep=>总步数,upicon_success_after_reload_auth=>上传头像后是否重新拉取权限[1=>yes,0=>no]
		private String req_after_popup_msg ;//请求后的弹框文案 //如果有出显该字段请按该字段的文案给出弹框提示
		private String CLTShowMenuFlag ;//在请求丘比特,趣友,附近时用于标识是否需要进入完善个人信息流程(有该标记时表示需要进入完善个人信息流程)
		private String deauth_text ;//权限受限弹框文案

		public String getUri_of_auth_deny()
		{
			return uri_of_auth_deny;
		}

		public void setUri_of_auth_deny(String uri_of_auth_deny)
		{
			this.uri_of_auth_deny = uri_of_auth_deny;
		}

		public String getReq_after_popup_msg()
		{
			return req_after_popup_msg;
		}

		public void setReq_after_popup_msg(String req_after_popup_msg)
		{
			this.req_after_popup_msg = req_after_popup_msg;
		}

		public String getCLTShowMenuFlag()
		{
			return CLTShowMenuFlag;
		}

		public void setCLTShowMenuFlag(String CLTShowMenuFlag)
		{
			this.CLTShowMenuFlag = CLTShowMenuFlag;
		}

		public String getDeauth_text()
		{
			return deauth_text;
		}

		public void setDeauth_text(String deauth_text)
		{
			this.deauth_text = deauth_text;
		}
	}
}
