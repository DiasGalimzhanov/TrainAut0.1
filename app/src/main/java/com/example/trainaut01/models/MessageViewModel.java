package com.example.trainaut01.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessageViewModel extends ViewModel {
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        List<Message> currentMessages = messages.getValue();
        currentMessages.add(message);
        messages.setValue(currentMessages);
    }
}
