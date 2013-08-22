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
    $result = mysql_query("SELECT noiseHuntID FROM noisehuntstate WHERE userID = $userID");
 
    if (!empty($result)) {
        if (mysql_num_rows($result) > 0) {
			$lastNoiseHunt = 0;
            while ($row = mysql_fetch_array($result)) {
				if($row["noiseHuntID"] > $lastNoiseHunt)
					$lastNoiseHunt = $row["noiseHuntID"];
			}
            $response["noiseHuntID"] = $lastNoiseHunt;
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            $response["success"] = 1;
			$response["noiseHuntID"] = 0;
            $response["message"] = "No noise hunt state found of that user.";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
		$response["success"] = 1;
		$response["noiseHuntID"] = 0;
		$response["message"] = "No noise hunt state found of that user.";
 
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