<%@ page import="controller.BaseController" %>
<%@ page import="core.utils.ParameterBag" %>
<%@ page import="core.annotations.Parameter" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: gidonr
  Date: 24/11/17
  Time: 14:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="request"/>
<jsp:useBean id="container" class="core.utils.Container" scope="request"/>

<jsp:useBean id="title" class="java.lang.String" scope="request"/>

<!DOCTYPE html>
<html>
<head>
    <title>${ title }</title>
    <meta http-equiv="content-type" charset="UTF-8"/>

    <link rel="stylesheet"
          href="<%= renderer.asset("@css/bootstrap.min.css") %>"
          type="text/css"/>
    <link rel="stylesheet"
          href="<%= renderer.asset("@css/style.css") %>"
          type="text/css"/>

    <%--<link rel="stylesheet"
          href="<%= getServletConfig().getServletContext().getContextPath() %>/assets/css/bootstrap.min.css"
          type="text/css"/>
    <link rel="stylesheet" href="<%= getServletConfig().getServletContext().getContextPath() %>/assets/css/style.css"
          type="text/css"/>--%>

    <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css"
          integrity="sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb" crossorigin="anonymous">--%>
</head>
<body>

<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
    <a class="navbar-brand" href="<%= router.build("index.home") %>">Home</a>

    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault"
            aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="dropdown-students" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Etudiants</a>
                <div class="dropdown-menu" aria-labelledby="dropdown-students">
                    <a class="dropdown-item" href="<%= router.build("student.list") %>">Voir les étudiants</a>
                    <a class="dropdown-item" href="<%= router.build("student.add") %>">Ajouter un étudiant</a>
                </div>
            </li>

            <%--<li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" id="dropdown-groups" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Groupes</a>
                <div class="dropdown-menu" aria-labelledby="dropdown-groups">
                    <a class="dropdown-item" href="<%= router.build("group.list") %>">Voir les groupes</a>
                    <a class="dropdown-item" href="<%= router.build("group.add") %>">Ajouter un groupe</a>
                </div>
            </li>--%>
        </ul>
        <%--<form class="form-inline my-2 my-lg-0">
            <input class="form-control mr-sm-2" type="text" placeholder="Search" aria-label="Search">
            <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
        </form>--%>
    </div>
</nav>

<main class="container">

    <%
    ParameterBag flashBag = (ParameterBag) request.getAttribute(BaseController.FLASH_BAG);
    for (Map.Entry<String, Object> entry : flashBag.entrySet()) {
    %>
    <div class="notification notification-<%= entry.getKey().toLowerCase() %>">
        <%= entry.getValue() %>
    </div>
    <%
    }
    %>