package cn.ucai.superwechat.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Contact;
import cn.ucai.superwechat.bean.Member;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.data.OkHttpUtils;

/**
 * Created by sks on 2016/5/31.
 */
public class DownLoadGroupMemberTask extends BaseActivity{
    public static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String hxid;
    String downloadContactListUrl;

    public DownLoadGroupMemberTask(Context mContext, String userName) {
        this.mContext = mContext;
        this.hxid = userName;
        initDownloadContactListUrl();
    }

    /*http://10.0.2.2:8080/SuperWeChatServer/Server?
    request=download_group_members&m_member_group_id=*/
    public void initDownloadContactListUrl() {
        try {
            downloadContactListUrl = new ApiParams().with(I.Member.MEMBER_ID, hxid)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS);
            Log.e(TAG,"downloadContactListUrl="+downloadContactListUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<Member[]>(downloadContactListUrl, Member[].class,
                responseDownloadMemberListListener(),errorListener()));
    }

    private Response.Listener<Member[]> responseDownloadMemberListListener() {
        return new Response.Listener<Member[]>() {
            @Override
            public void onResponse(Member[] members) {
                Log.e(TAG,"responseDownloadMemberListListener,Member="+members);
                if (members == null) {
                    return;
                }
                Log.e(TAG,"responseDownloadMemberListListener,Member.size="+members.length);
                ArrayList<Member> list = OkHttpUtils.array2List(members);
                HashMap<String, ArrayList<Member>> groupMembers = SuperWeChatApplication.getInstance().getGroupMembers();
                ArrayList<Member> memberArrayList = groupMembers.get(hxid);
                if (memberArrayList != null) {
                    memberArrayList.clear();
                    memberArrayList.addAll(list);
                } else {
                    groupMembers.put(hxid, list);
                }
                mContext.sendStickyBroadcast(new Intent("update_member_List"));
            }
        };
    }

}
