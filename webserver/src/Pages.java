package bibleServer;

/** Strings that can be sent as basic 'html files' so I don't have to store their content as a real file. */
class Pages {

    public static final String NOT_FOUND = (
        String.join(
            "\n",
            "<html style='text-align:center'>",
            "    <h1>404 not found</h1>",
            "</html>"
        )
    );

    public static final String WELCOME = (
        String.join(
            "\n",
            "<html>",
            "<style>",
            "    img {display:block; margin-left:auto; margin-right:auto}",
            "    body {text-align:center}",
            "</style>",
            "<body>",
            "    <h1> Welcome to the bibleServer web server.</h1>",
            "    Make your requests by typing in the appropriate URL. This is just the backend.",
            "</body>",
            "</html>"
        )
    );
}
