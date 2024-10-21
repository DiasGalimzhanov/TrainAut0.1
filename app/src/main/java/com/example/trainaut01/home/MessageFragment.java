package com.example.trainaut01.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.trainaut01.R;
import com.example.trainaut01.adapter.MessageAdapter;
import com.example.trainaut01.models.Message;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация списка сообщений
        messageList = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerViewMessages = view.findViewById(R.id.recycler_view_messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        // Зарегистрируйте приемник для получения новых сообщений
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(messageReceiver, new IntentFilter("NewMessage"));

        return view;
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message newMessage = (Message) intent.getSerializableExtra("message");
            messageList.add(newMessage);
            messageAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(messageReceiver);
    }

}
