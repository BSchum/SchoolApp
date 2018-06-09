package com.ynov.bsc.Services;

/**
 * Created by Brice on 27/03/2018.
 */

public interface AsyncResponce {

    /// Cette classe permet de realiser une callback dans une Async Task en overidant cette classe
    void ComputeResult(Object result);
}
