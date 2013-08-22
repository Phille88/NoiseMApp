<?php

 require_once '/home/thesis-std/request_key.php';
 
if(isset($_POST['requestKey']) && $_POST["requestKey"] == REQUESTKEY){
	 
	// include db connect class
	require_once __DIR__ . '/db_connect.php';
	require_once '/home/thesis-std/db_config.php';
	 
	// connecting to db
	$db = new DB_CONNECT();

/* 
    Author - Ofri Markus 
    Date   - 3/12/03 
    
    This is a generic script to view and modify mysql databases. 
    All you need to do to use this script is: 
    1. Put it in your site, and call the file "admin.php". 
    2. Fill in the connection details on the first line, replace DB_USER AND DB_PASSWORD. 
    3. Fill in the database name on 2 lines, replace DB_NAME. 
    
    The advantage of this script is that is you don't need to 
    modify it to your own scheme. it will work on any scheme. 
    
    I would be happy to receive comments and improved versions of 
    this script to: 
    
    markus_ofri@hotmail.com 

    Enjoy! 
    
*/ 

// Because the first time we enter the site we have'nt selected 
// a table to view we init the number of rows in the current table 
$rowNum=0; 

?> 

<html> 
<body bgcolor="#ffffff"> 

<? 

// Get the data of the tables on the scheme 
$result = mysql_list_tables (DB_DATABASE); 

$i=0; 
while ($i < mysql_num_rows ($result)) 
{ 
    $tb_names[$i] = mysql_tablename ($result, $i);$i++; 
} 

// Check if we got here after pressing submit on the page 
if (isset($_POST['submit'])) 
{ 
   // If we did press the submit button, we sould view the table that was on the select 
   // box 
   $submit=$_POST['submit']; 
   $table=$_POST['table']; 
} 
else 
{ 
    // If we didn't get here after pressing the submit button, check if we already 
    // viewed one of the tables (and saved it's name on the hidden field hidtable 
       if (isset($_POST['hidtable'])) 
    { 
        $table = $_POST['hidtable']; 
    } 
    else 
    { 
        $table="<i>not selected</i>";$xnum=0; 
    } 
} 

?> 

<center> 
<form name=ff method=post action="/admin.php"> 
<b>Select table</b>:<select name="table"> 
<? 
for($x=0;$x<$i;$x++) 
{?> 
    <option value="<? echo $tb_names[$x];?>" <? if (isset($table) && $table==$tb_names[$x]) {echo " selected ";} ?>><? echo $tb_names[$x];?></option> 
<? }?> 
</select> 
<input type="submit" name="submit" value="submit"> 



<? 

// Check if we pressed the submit button and if we did - fetch the table data 
if(isset($submit) || isset($_POST['hidtable'])){ 
$SQL="SELECT * FROM $table"; 
$result = mysql_query($SQL); 
$xnum = mysql_num_fields($result); 
$rowNum = mysql_num_rows($result); 

// Read all the data in the table 
for ($j = 0; $j<$rowNum; ++$j) 
{ 
    $row = mysql_fetch_array($result); 
    $currTable[$j]=$row; 
} 
} 




echo "<b><center>$table contains $xnum fields.<br><br></b></center>";?> 

<center> 
<table bgcolor=black><tr> 
<td bgcolor="#e3e3e3">Offset</td><td bgcolor="#e3e3e3">Field Name</td><td bgcolor="#e3e3e3">Field type</td></tr> 


<? 
// Get the data about the primary keys and the numeric fields in the table 
for($x=0;$x<$xnum;$x++) 
{ 
    $name[$x]=mysql_field_name($result,$x);$type[$x]=mysql_field_type($result,$x); 
    $currField = mysql_fetch_field($result,$x); 
    $key[$x]=$currField->primary_key; 
    $numeric[$x]=$currField->numeric; 
?> 

<tr> 
<td bgcolor="white"><? echo $x;?></td> 
<td bgcolor="white"><? echo $name[$x];?></td> 
<td bgcolor="white"><? echo $type[$x];?></td> 
</tr> 
<? }?> 
</table></center> 
<br> 

<? 

// Check to see if there was an update to a row 
for ($j = 0; $j<$rowNum; ++$j) 
{ 
    if (isset($_POST["update".$j])) 
    { 
        // Make an sql update query 
        echo "<center>There was an update to row $j</center>"; 
        $sql="update $table set "; 
        for ($i = 0; $i < $xnum; ++$i) 
        { 
            if ($numeric[$i]==1) 
            { 
                $sql.=$name[$i]."=".$_POST["$name[$i]".$j]." "; 
            } 
            else 
            { 
                $sql.=$name[$i]."='".$_POST["$name[$i]".$j]."' ";            
            } 
            
            if ($i != $xnum-1) {$sql.=",";} 
        } 
        $sql.="WHERE "; 
        $notFirstKey = 0; 
        for ($i = 0; $i < $xnum; ++$i) 
        {    
            if ($key[$i]==1) 
            { 
                if ($notFirstKey == 0) {$notFirstKey=1;} 
                else {$sql.=" AND ";} 
                $sql.=$name[$i]."=".$currTable[$j][$i]; 
                
            } 
            
        } 
        if ($notFirstKey == 0) { 
            echo "Table does not have a primary key, not doing anything"; 
        } 
        else { 
              echo $sql; 
            $result = mysql_query($sql); 
        } 

        
    } 
} 

// Check to see if there was a delete to a row 
for ($j = 0; $j<$rowNum; ++$j) 
{ 
    if (isset($_POST["delete".$j])) 
    { 
        echo "<center>There was an delete to row $j</center>"; 
        $sql="delete from $table "; 
        $sql.="WHERE "; 
        $notFirstKey = 0; 
        for ($i = 0; $i < $xnum; ++$i) 
        {    
            if ($key[$i]==1) 
            { 
                if ($notFirstKey == 0) {$notFirstKey=1;} 
                else {$sql.=" AND ";} 
                $sql.=$name[$i]."=".$currTable[$j][$i]; 
                
            } 
            
        } 
        if ($notFirstKey == 0) { 
            echo "Table does not have a primary key, not doing anything"; 
        } 
        else { 
              echo $sql; 
            $result = mysql_query($sql); 
        } 
        
    } 
} 

// Check to see if there was an insert of a row 
    if (isset($_POST["insert"])) 
    { 
        echo "<center>There was an insert of a row </center>"; 
        $sql="insert into $table values ("; 
        for ($i = 0; $i < $xnum; ++$i) 
        { 
            $sql.="'".$_POST["$name[$i]"."insert"]."' "; 
            if ($i != $xnum-1) {$sql.=",";} 
        } 
        $sql.=")"; 
          echo $sql; 
        $result = mysql_query($sql); 
        
    } 




/////////////// 







if(isset($submit) || isset($_POST['hidtable'])){ 
$SQL="SELECT * FROM $table"; 
$result = mysql_query($SQL); 
$xnum = mysql_num_fields($result); 
$rowNum = mysql_num_rows($result); 

} 

?> 




<center> 
<table bgcolor=black> 
<tr> 
<? 
for ($i=0; $i<$xnum; ++$i) 
{ 
$name[$i]=mysql_field_name($result,$i); 
?> 
<td bgcolor="#e3e3e3"><? echo $name[$i]; ?></td>    
<? } ?> 
</tr> 

<? for ($j = 0; $j<$rowNum; ++$j) 
{ 
$row = mysql_fetch_array($result); 
$currTable[$j]=$row; 
} 
?> 


<? for ($j = 0; $j<$rowNum; ++$j) 
{ 
?> 
<tr> 
<? for ($i=0; $i<$xnum; ++$i) 
{ 
?> 
<td><input name=<? echo "\"$name[$i]".$j."\""; ?> type="text" id=<? echo "\"$name[$i]\""; ?> value=<? $currRow=$currTable[$j]; echo "\"$currRow[$i]\""; ?>></td> 

<? } ?> 
<td><input type="submit" name=<? echo "\"update".$j."\""; ?> value=<? echo "\"update\""; ?> height="10"> 
    <input type="submit" name=<? echo "\"delete".$j."\""; ?> value=<? echo "\"delete\""; ?> height="10"> 
</td> 
</tr> 
<? } 
for ($i=0; $i<$xnum; ++$i) 
{ 
?> 
<td><input name=<? echo "\"$name[$i]"."insert\""; ?> type="text" id=<? echo "\"$name[$i]\""; ?>></td> 

<? } ?> 

<td><input type="submit" name="insert" value="insert"></td> 




</table> 

</center> 
<input type="hidden" name="hidtable" value=<? echo "\"$table\""; ?>> 


</form> 
</center> 
</body> 
</html>
<? }
?>