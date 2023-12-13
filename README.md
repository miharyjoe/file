# FileSystem Application for handling Error
## Description
An application to upload and download file , in this application we implements handling error
as many possible
## Requierement:
* Java 17 
* IntelIj

## How to launch the project
 - Clone the repostiory project:
   ```bash
   git clone -b feature/java https://github.com/hei-school/cc-hei-hub-bughunter.git
   ```
 - Open the file in IntelIj and launch the app
 - Open in Postman to make a request:
  - the url : http://localhost:8080/
     - Add the following parameters before you launch a post request in postman
          * Add in Header: Content-Type : multipart/form-data
          * Add in Body: file change the type to file and upload your file


Note: the file upload is put in upload-dir