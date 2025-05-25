package com.dundunteam.service;

public interface wxService {
      String wxDecrypt(String encryptedData,String sessionId,String vi) throws Exception;
}
