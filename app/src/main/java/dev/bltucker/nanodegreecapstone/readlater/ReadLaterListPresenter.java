package dev.bltucker.nanodegreecapstone.readlater;

import javax.inject.Inject;

public class ReadLaterListPresenter {

    private final ReadLaterRepository repository;

    private ReadLaterListView view;

    @Inject
    public ReadLaterListPresenter(ReadLaterRepository repository){
        this.repository = repository;
    }


    public void onViewCreated(ReadLaterListView listView){
        view = listView;
    }

    public void onViewRestored(ReadLaterListView listView){
        view = listView;
    }




}
