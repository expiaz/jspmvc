<%@ page import="core.utils.ParameterBag" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="request"/>

<jsp:useBean id="student" class="entity.Etudiant" scope="request"/>

<h1>${ student.nom }</h1>
<h2>${ student.prenom }</h2>
<a href="<%= router.build("student.edit", new ParameterBag().add("student", student.getId())) %>">Edit</a>