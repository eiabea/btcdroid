package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Worker implements Parcelable {

	// Attributes
	private String name;
	private long last_share;
	private String score;
	private boolean alive;
	private int send_threshold;
	private int hashrate;

	// Standardconstructor
	public Worker() {}

	// Constructor used for Parcelable
	public Worker(Parcel in) {
		name = in.readString();
		last_share = in.readLong();
		score = in.readString();
		alive = in.readByte() == 1;
		send_threshold = in.readInt();
		hashrate = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeLong(last_share);
		dest.writeString(score);
		dest.writeByte((byte) (alive ? 1 : 0));
		dest.writeInt(send_threshold);
		dest.writeInt(hashrate);

	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public Worker createFromParcel(Parcel in) {
			return new Worker(in);
		}

		public Worker[] newArray(int size) {
			return new Worker[size];
		}
	};

	public long getLast_share() {
		return last_share;
	}

	public void setLast_share(long last_share) {
		this.last_share = last_share;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public int getSend_threshold() {
		return send_threshold;
	}

	public void setSend_threshold(int send_threshold) {
		this.send_threshold = send_threshold;
	}

	public int getHashrate() {
		return hashrate;
	}

	public void setHashrate(int hashrate) {
		this.hashrate = hashrate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

}
