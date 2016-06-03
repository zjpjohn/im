package cn.xyz.commons.constants;

import cn.xyz.commons.vo.JSONMessage;

/**
 * 常量
 * 
 * @author luorc
 * 
 */
public interface KConstants {

	public interface Expire {
		static final int DAY1 = 86400;
		static final int DAY7 = 604800;
		static final int HOUR12 = 43200;
	}

	public interface MsgType {
		static final Byte Audio = 3;
		static final Byte Image = 2;
		static final Byte Image_Audio = 5;
		static final Byte Text = 1;
		static final Byte Video = 4;
	}

	public interface Result {
		static final JSONMessage InternalException = new JSONMessage(1020101,
				"接口内部异常");
		static final JSONMessage ParamsAuthFail = new JSONMessage(1010101,
				"请求参数验证失败，缺少必填参数或参数错误");
		static final JSONMessage TokenEillegal = new JSONMessage(1030101,
				"缺少访问令牌");
		static final JSONMessage TokenInvalid = new JSONMessage(1030102,
				"访问令牌过期或无效");
	}

	public interface ResultCode {
		static final int Failure = 0;
		static final int InternalException = 1020101;
		static final int ParamsAuthFail = 1010101;
		static final int ParamsLack = 1010102;
		static final int Success = 1;
	}

}
