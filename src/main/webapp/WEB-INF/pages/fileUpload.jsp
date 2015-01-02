<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Data Manager</title>
</head>
<body>
  <div class="row">
    <div class="col-12 col-sm-12 col-lg-12">
      <h3>Data Manager</h3>
      
      <c:if test="${message != null}">
        <div class="alert alert-danger">${message}</div>
      </c:if>
    
      <div class="well">
        <form:form role="form" method="POST" commandName="fileUpload" action="${pageContext.request.contextPath}/fileUpload" enctype="multipart/form-data">
          <div class="form-group">
            <label for="file">Please select an excel file (*.xlsx) to upload</label>
            <input type="file" name="file" />
            <p class="help-block">Uploaded file's data is extracted and saved into a file in JSON format.</p>
          </div>
          <input type="submit" value="upload" class="btn btn-primary" />
        </form:form>
      </div>
    
      <table class="table table-striped table-hover table-condensed table-responsive">
        <thead>
          <tr>
            <th>Uploaded Files</th>
            <th>Download</th>
            <th>Delete</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="uploadedFile" items="${uploadedFiles}">
            <tr>
              <td>
                <a href="${pageContext.request.contextPath}/fileUpload/download/${uploadedFile.name}">
                  ${uploadedFile.name}
                </a>
              </td>
              <td>
                <a href="${pageContext.request.contextPath}/fileUpload/download/${uploadedFile.name}">
                  <span class="glyphicon glyphicon-download-alt"></span> Download
                </a>
              </td>
              <td>
                <a href="${pageContext.request.contextPath}/fileUpload/delete/${uploadedFile.name}">
                  <span class="glyphicon glyphicon-floppy-remove"></span> Delete
                </a>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    
    </div>
    <!--/span-->
  </div>
  <!--/row-->
</body>
</html>