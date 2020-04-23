package me.ahaliulang.testdemo.factory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lgh on 2018/9/10
 */
public class CardStyleInfo
{
	public List<CardStyleBitmapInfo> bitmapInfos = new ArrayList<>(); //第一个一定要是个人头像
	public String qrUrl; //二维码地址
	public String nickName; //昵称
	public String introduction;//简介
	public String popsNum; // 人气
	public String fansNum; //粉丝
	public String worksNum; //作品
	public String address;//定位地址
	public String content;//分享内容
	public String title;//标题
	public String time;//作品发布时间
	public boolean kol;//是否是达人，true 是，false 反之
	public boolean isShowMedal;//是否显示勋章，true是，false反之
	public String show_logo;//分享模板的商业logo 中南：zhongnanBusinessActivities
}
