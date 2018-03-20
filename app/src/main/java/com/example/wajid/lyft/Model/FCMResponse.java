package com.example.wajid.lyft.Model;

import java.util.List;

//import javax.xml.transform.Result;

/**
 * Created by wajid on 13-Mar-18.
 */

public class FCMResponse {

    public long multi_cast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;


    public FCMResponse() {
    }

    public FCMResponse(long multi_cast_id, int success, int failure, int canonical_ids, List<Result> results) {
        this.multi_cast_id = multi_cast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    public long getMulti_cast_id() {
        return multi_cast_id;
    }

    public void setMulti_cast_id(long multi_cast_id) {
        this.multi_cast_id = multi_cast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
