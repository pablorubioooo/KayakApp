package com.example.pablo.kayakapp.etc.frequency;

public interface DataMultiSource extends DataSource
 {
  public DataMultiSource addConsumer(DataConsumer c);
  public DataMultiSource addConsumer(DataConsumer c, PieceOfData.Tag tag);
  }
