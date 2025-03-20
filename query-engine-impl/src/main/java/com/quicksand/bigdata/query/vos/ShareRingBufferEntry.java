package com.quicksand.bigdata.query.vos;

import com.quicksand.bigdata.query.ringbuffer.ShareRingBuffer;

/**
 * ShareRingBufferEntry
 *
 * @author xupei
 * @date 2022/8/11
 */
public interface ShareRingBufferEntry<T extends UniqFlaged> {

    void setCurBuffer(ShareRingBuffer<T> shareRingBuffer);

    void persistence();

    T reload();

}
