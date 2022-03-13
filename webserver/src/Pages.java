package bibleServer;

/** Strings that can be sent as basic 'html files' so I don't have to store their content as a real file. */
class Pages {

    public static final String NOT_FOUND = String.join("\n",

                                            "<html style='text-align:center'>",
                                            "    <h1>404 not found</h1>",
                                            "</html>"
    
                                        );

    public static final String WELCOME = String.join("\n",

                    "<html>",
                    "<style>",
                    "    img {display:block; margin-left:auto; margin-right:auto}",
                    "    body {text-align:center}",
                    "</style>",
                    "<body>",
                    "    <h1> Welcome to the httpRAPL web server.</h1>",
                    "    Make your requests by typing in the appropriate URL. This is just a backend",
                    "    so I'm not incorporating a whole user interface for it.",
                    "</body>",
                    "</html>"

                );
                    // "    <br><br><br>",
                    // "    <img src=\"/home/alejareact-rapl/backendfiles/woahfish.jpeg\">",
                    // "    <br><br>",
                    // "    I hope you're not doing anything <b><u><i>fishy</i></u></b> because I have snipers",
                    // "    aimed at every single one of your windows so really consider the",
                    // "    next course of action wisely, kiddo",
}
