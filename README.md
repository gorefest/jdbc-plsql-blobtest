# PL/SQL -  Blob Example
## CREATE DATABASE PACKAGE
Create the destination table
```
Create Table Blobtable(MyID Number, Blobdata Blob, blobsize NUMBER);
```
Create the blob package header
```
CREATE OR REPLACE package InsertBlob as
  PROCEDURE TestBlobInsert (my IN NUMBER, BlobParam in blob);
end InsertBlob;
```
Create the blob package body 
```
CREATE OR REPLACE package body InsertBlob as
  PROCEDURE TestBlobInsert (my IN NUMBER, BlobParam in blob)
  as
    begin
      INSERT INTO blobtable (myid,blobdata, blobsize) values(my,BlobParam, dbms_lob.getlength(BlobParam));
    end TestBlobInsert;
end InsertBlob;
```
After testing, simply verify using 
```
select * from Blobtable
```