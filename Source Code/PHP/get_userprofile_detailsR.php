<?php 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for post data
if (isset($_POST['userID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	 
	// connecting to db
	$db = new DB_CONNECT();
	
    $userID = mysql_real_escape_string($_POST['userID']);
 
    // get a userprofile from userprofiles table
    $result = mysql_query("SELECT * FROM userprofilesr WHERE userID = $userID");
 
    if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            //$userprofile = array();
			$userprofile["userID"] = $result["userID"];
            $userprofile["googleID"] = $result["googleID_1"].$result["googleID_2"];
            $userprofile["firstName"] = $result["firstName"];
            $userprofile["lastName"] = $result["lastName"];
            $userprofile["email"] = $result["email"];
            $userprofile["createdAt"] = $result["createdAt"];
            $userprofile["updatedAt"] = $result["updatedAt"];
			
            // success
            $response["success"] = 1;
            $response["userprofile"] = $userprofile;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no userprofile found
            $response["success"] = 0;
            $response["message"] = "No user profile found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no userprofile found
        $response["success"] = 0;
        $response["message"] = "No user profile found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>