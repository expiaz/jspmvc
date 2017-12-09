<%--
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
            <li class="nav-item">
                <a class="nav-link" href="<%= router.build("student.list") %>">Voir les étudiants</a>
            </li>
            <li>
                <a class="nav-link" href="<%= router.build("student.add") %>">Ajouter un étudiant</a>
            </li>

            <!--<li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="http://example.com" id="dropdown01" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Dropdown</a>
                <div class="dropdown-menu" aria-labelledby="dropdown01">
                    <a class="dropdown-item" href="#">Action</a>
                    <a class="dropdown-item" href="#">Another action</a>
                    <a class="dropdown-item" href="#">Something else here</a>
                </div>
            </li>-->
        </ul>
        <%--<form class="form-inline my-2 my-lg-0">
            <input class="form-control mr-sm-2" type="text" placeholder="Search" aria-label="Search">
            <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
        </form>--%>
    </div>
</nav>

<main class="container">