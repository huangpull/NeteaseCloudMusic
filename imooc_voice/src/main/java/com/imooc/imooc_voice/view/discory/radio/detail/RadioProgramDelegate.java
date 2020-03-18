package com.imooc.imooc_voice.view.discory.radio.detail;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.imooc.imooc_voice.R;
import com.imooc.imooc_voice.R2;
import com.imooc.imooc_voice.api.RequestCenter;
import com.imooc.imooc_voice.model.newapi.dj.DjProgramBean;
import com.imooc.imooc_voice.model.radio.RadioProgramLoadEvent;
import com.imooc.lib_common_ui.delegate.NeteaseDelegate;
import com.imooc.lib_network.listener.DisposeDataListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class RadioProgramDelegate extends NeteaseDelegate {

    @BindView(R2.id.rv_delegate_normal)
    RecyclerView mRecyclerView;

    private static final String ARGS_RADIO_ID = "ARGS_RADIO_ID";
    private String id;
    private RadioProgramAdapter mAdapter;

    private ILoadFinishListener listener;

    static RadioProgramDelegate newInstance(String id){
        final Bundle args = new Bundle();
        args.putString(ARGS_RADIO_ID, id);
        final RadioProgramDelegate delegate = new RadioProgramDelegate();
        delegate.setArguments(args);
        return delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        if (args != null) {
            id = args.getString(ARGS_RADIO_ID);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public Object setLayout() {
        return R.layout.delegate_recyclerview_normal;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            if(listener != null){
                listener.onLoadFinish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, @NonNull View view) throws Exception {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                RequestCenter.getRadioProgram(id, new DisposeDataListener() {
                    @Override
                    public void onSuccess(Object responseObj) {
                        DjProgramBean bean  = (DjProgramBean) responseObj;
                        List<DjProgramBean.ProgramsBean> programs = bean.getPrograms();
                        mAdapter = new RadioProgramAdapter(programs);
                        mRecyclerView.setAdapter(mAdapter);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    }

                    @Override
                    public void onFailure(Object reasonObj) {

                    }
                });
                return null;
            }
        }.execute();

    }

    @Override
    public void post(Runnable runnable) {

    }


    public void setListener(ILoadFinishListener listener) {
        this.listener = listener;
    }

    interface ILoadFinishListener{
        void onLoadFinish();
    }

    static class RadioProgramAdapter extends BaseQuickAdapter<DjProgramBean.ProgramsBean, BaseViewHolder>{

        RadioProgramAdapter(@Nullable List<DjProgramBean.ProgramsBean> data) {
            super(R.layout.item_radio_program, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder adapter, DjProgramBean.ProgramsBean bean) {

            adapter.setText(R.id.item_radio_program_toptext, bean.getName());
            adapter.setText(R.id.item_radio_program_no, String.valueOf(adapter.getLayoutPosition()+1));
            adapter.setText(R.id.item_radio_program_createtime, timeStamp2Date(String.valueOf(bean.getCreateTime()), "yyyy-MM-dd"));
            adapter.setText(R.id.item_radio_program_playnum, String.valueOf(bean.getListenerCount()));
            adapter.setText(R.id.item_radio_program_duration, timeStamp2Date(String.valueOf(bean.getDuration()), "HH:mm:ss"));
        }

        static String timeStamp2Date(String seconds, String format) {
            if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
                return "";
            }
            if(format == null || format.isEmpty()){
                format = "yyyy-MM-dd HH:mm:ss";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(new Date(Long.valueOf(seconds+"000")));
        }
    }
}