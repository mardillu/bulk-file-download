package com.mardillu.bulkdownloader;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.RestrictTo;

public class DownloadStatusModel implements Parcelable {
    int success, failure, total;
    String value;
    LocalData dbLocalData;

    public DownloadStatusModel(String value, LocalData dbLocalData) {
        this.dbLocalData = dbLocalData;
        this.value = value;
        this.total = getPersistedTotal();
        this.success = getPersistedSuccess();
        this.failure = getPersistedFailure();
    }

    protected DownloadStatusModel(Parcel in) {
        success = in.readInt();
        failure = in.readInt();
        total = in.readInt();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(success);
        dest.writeInt(failure);
        dest.writeInt(total);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadStatusModel> CREATOR = new Creator<DownloadStatusModel>() {
        @Override
        public DownloadStatusModel createFromParcel(Parcel in) {
            return new DownloadStatusModel(in);
        }

        @Override
        public DownloadStatusModel[] newArray(int size) {
            return new DownloadStatusModel[size];
        }
    };

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private int getPersistedSuccess() {
        return dbLocalData.getIntegerPreferenceValue(value + "Success");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private void setSuccess(int success) {
        this.success = success;
        dbLocalData.setIntegerPreferenceValue(value + "Success", success);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private int getPersistedFailure() {
        return dbLocalData.getIntegerPreferenceValue(value + "Failure");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private void setFailure(int failure) {
        this.failure = failure;
        dbLocalData.setIntegerPreferenceValue(value + "Failure", success);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public int getPersistedTotal() {
        return dbLocalData.getIntegerPreferenceValue(value + "Total");
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void setTotal(int total) {
        this.total = total;
        dbLocalData.setIntegerPreferenceValue(value + "Total", total);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    synchronized int increaseSuccess() {
        setSuccess(getPersistedSuccess() + 1);
        return getPersistedSuccess();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    synchronized int increaseFailure() {
        setFailure(getPersistedFailure() + 1);
        return getPersistedFailure();
    }

    @Override
    public String toString() {
        return "DownloadStatusModel{" +
                "success=" + success +
                ", failure=" + failure +
                ", total=" + total +
                ", value='" + value + '\'' +
                '}';
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public void resetState() {
        dbLocalData.setIntegerPreferenceValue(value + "Failure", 0);
        dbLocalData.setIntegerPreferenceValue(value + "Success", 0);
        dbLocalData.setIntegerPreferenceValue(value + "Total", 0);
    }

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public int getTotal() {
        return total;
    }
}
