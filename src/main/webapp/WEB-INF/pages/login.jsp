<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row">
  <div class="col-12 col-sm-12 col-lg-12">
    <div class="container">

      <c:if test="${not empty error}">
        <div class="alert alert-danger">
          Your login attempt was not successful, try again.<br /> Caused : ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
        </div>
      </c:if>

      <form role="form" name="f" action="<c:url value='j_spring_security_check' />" method="POST">
        <div class="form-group">
          <input type="text" class="form-control" placeholder="Username" name="j_username" required>
        </div>
        <div class="form-group">
          <input type="password" class="form-control" placeholder="Password" name="j_password" required>
        </div>
        <button type="submit" class="btn btn-primary">Submit</button>
      </form>
    </div>
  </div><!--/span-->
</div><!--/row-->