<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<jsp:useBean id="router" class="core.http.Router" scope="request"/>

<ul>
    <li>
        <a href="<%= router.build("student.list") %>">Voir les étudiants</a>
    </li>
</ul>