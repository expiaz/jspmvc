<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="error" class="java.lang.String" scope="request"/>

<h1>Ajouter un groupe</h1>

<form action="<%= router.build("group.add") %>" method="post">
    <input type="text" name="nom" placeholder="Nom">
    <br/>
    <button>Ajouter</button>
</form>

<%--
<ul>
    <c:forEach items="${students}" var="student">
        <li>${ student.name }</li>
    </c:forEach>
</ul>
--%>