<jsp:useBean id="router" class="core.http.Router" scope="request"/>
<jsp:useBean id="renderer" class="core.utils.Renderer" scope="request"/>

<jsp:useBean id="name" class="java.lang.String" scope="request"/>

<h1>Hello <%= name %> !</h1>
<a href="<%= router.build("index.hello", new String[][] {
    new String[] {"name", "jean"}
}) %>">Link</a>