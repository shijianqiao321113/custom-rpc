package com.rpc.message;

public class MessageHead {
	
	/*0xAB 表示心跳包   0xBC 表示数据包*/
	private byte packageType;

	/*消息标志 0xAB 表示请求包    0xBC 表示响应包*/
    private byte type;
    
    /*消息内容长度*/
    private Integer contentLength;
    
    /*消息ID*/
    private long messageId;
    
    public MessageHead(byte packageType,byte type, Integer contentLength,long messageId) {
    	this.packageType = packageType;
    	this.type = type;
        this.contentLength = contentLength;
        this.messageId = messageId;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getPackageType() {
		return packageType;
	}

	public void setPackageType(byte packageType) {
		this.packageType = packageType;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

}
