/**
 * Tv_updateSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface Tv_updateSoap extends java.rmi.Remote {
    public java.lang.String voteSongById(java.lang.String username, java.lang.String password, java.lang.String keyword, java.lang.String tel, java.lang.String poinToVote) throws java.rmi.RemoteException;
    public java.lang.String voteSongByName(java.lang.String username, java.lang.String password, java.lang.String keyword, java.lang.String tel, java.lang.String poinToVote) throws java.rmi.RemoteException;
    public java.lang.String searchSongHot(java.lang.String username, java.lang.String password, java.lang.String keyword, int numerOfSong) throws java.rmi.RemoteException;
    public java.lang.String searchSongName(java.lang.String username, java.lang.String password, java.lang.String keyword, int numerOfSong) throws java.rmi.RemoteException;
}
