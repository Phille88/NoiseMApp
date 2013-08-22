
<!--
To change this template, choose Tools | Templates
and open the template in the editor.
-->
<!DOCTYPE html>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
        <script type="text/javascript">
            $(document).ready(function(){
            });
        </script>
    </head>
    <body>
        <form name="" method="post" action="register.php">
			<input type="text" name="userID"/>
			<input type="text" name="email"/>
			<input type="text" name="regId"/>
			<input type="submit"/>
		</form>
    </body>
</html>

<?php
require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();
  


echo _isCurl();
function _isCurl(){
    return function_exists('curl_version');
}
?>
