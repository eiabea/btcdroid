package com.eiabea.btcdroid.util;

import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;

class BtcDroidPolicy implements RetryPolicy {

    /**
     * The current timeout in milliseconds.
     */
    private int mCurrentTimeoutMs;

    /**
     * The current retry count.
     */
    private int mCurrentRetryCount;

    /**
     * The maximum number of attempts.
     */
    private final int mMaxNumRetries;

    /**
     * The backoff multiplier for for the policy.
     */
    private final float mBackoffMultiplier;

    /**
     * The default socket timeout in milliseconds
     */
    public static final int DEFAULT_TIMEOUT_MS = 7000;

    /**
     * The default number of retries
     */
    public static final int DEFAULT_MAX_RETRIES = 1;

    /**
     * The default backoff multiplier
     */
    public static final float DEFAULT_BACKOFF_MULT = 1f;

    /**
     * Constructs a new retry policy using the default timeouts.
     */
    public BtcDroidPolicy() {
        mCurrentTimeoutMs = DEFAULT_TIMEOUT_MS;
        mMaxNumRetries = DEFAULT_MAX_RETRIES;
        mBackoffMultiplier = DEFAULT_BACKOFF_MULT;
    }

    /**
     * Returns the current timeout.
     */
    @Override
    public int getCurrentTimeout() {
        return mCurrentTimeoutMs;
    }

    /**
     * Returns the current retry count.
     */
    @Override
    public int getCurrentRetryCount() {
        return mCurrentRetryCount;
    }

    /**
     * Prepares for the next retry by applying a backoff to the timeout.
     *
     * @param error The error code of the last attempt.
     */
    @Override
    public void retry(VolleyError error) throws VolleyError {
        mCurrentRetryCount++;
        mCurrentTimeoutMs += (mCurrentTimeoutMs * mBackoffMultiplier);
        if (!hasAttemptRemaining()) {
            throw error;
        }
    }

    /**
     * Returns true if this policy has attempts remaining, false otherwise.
     */
    protected boolean hasAttemptRemaining() {
        return mCurrentRetryCount <= mMaxNumRetries;
    }

}
