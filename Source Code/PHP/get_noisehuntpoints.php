<?php
 
/*
 * Following code will create a new profile row
 * All profile details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
require_once '/home/thesis-std/request_key.php';
 
// check for required fields
if (isset($_POST['userID']) && isset($_POST['noiseHuntID']) && isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY) {
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    $userID = mysql_real_escape_string($_POST['userID']);
    $noiseHuntID = mysql_real_escape_string($_POST['noiseHuntID']);
	
	//DO A RECORDING
	$response["points"] = array();
	$response["badges"] = array();
	
	if($noiseHuntID == 1){
		$points["amount"] = 100;
		$points["description"] = "You successfully finished Walk In The Park!";
		$badge["badgeID"] = 2001;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge for finishing this NoiseHunt!";
	}
	elseif($noiseHuntID == 2){
		$points["amount"] = 200;
		$points["description"] = "You successfully finished Blitzkrieg!";
		$badge["badgeID"] = 2002;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge for finishing this NoiseHunt!";
	}
	elseif($noiseHuntID == 3){
		$points["amount"] = 350;
		$points["description"] = "You successfully finished Party Time!";
		$badge["badgeID"] = 2003;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge for finishing this NoiseHunt!";
	}
	elseif($noiseHuntID == 4){
		$points["amount"] = 500;
		$points["description"] = "You successfully finished Riverside!";
		$badge["badgeID"] = 2004;
		$badge["amount"] = 50;
		$badge["description"] = "You earned a badge for finishing this NoiseHunt!";
	}
	array_push($response["points"], $points);
	array_push($response["badges"], $badge);
 
	$pointsAmount = $points["amount"] + $badge["amount"];
	$update_points = mysql_query("INSERT INTO totalpoints (userID, totalPoints) VALUES ($userID, $pointsAmount) ON DUPLICATE KEY UPDATE totalPoints = totalPoints + $pointsAmount");
	$badgeID = $badge["badgeID"];
	$update_badges = mysql_query("INSERT INTO badges (userID, badgeID) VALUES ($userID, $badgeID)");
	
	if($update_points) {
		$response["success"] = 1;
		$response["message"] = $pointsAmount . " points are succesfully stored.";
		$response["totalPoints"] =  $pointsAmount;
	}
	else{
		$response["success"] = 0;
		$response["message"] = "Something went wrong :s";
	}
 
	// echoing JSON response
	echo json_encode($response);

} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>