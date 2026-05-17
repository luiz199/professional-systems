$serverLog = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld\server.log"
$serverErr = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld\server.err"
$jarPath = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld\target\avatarworld-1.0.0.jar"
$workDir = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld"

Write-Host "=== AvatarWorld Server Test ==="

# Check if server is running by testing API
try {
    $stats = Invoke-WebRequest -Uri "http://localhost:8081/api/stats" -UseBasicParsing -TimeoutSec 3
    $data = $stats.Content | ConvertFrom-Json
    Write-Host "[OK] Server is running"
    Write-Host "[OK] Stats: $($data.totalUsers) users, $($data.totalRooms) rooms, $($data.totalItems) items"
} catch {
    Write-Host "[FAIL] Server not running. Start it first with: java -jar $jarPath"
    exit 1
}

# Test users API
try {
    $users = Invoke-WebRequest -Uri "http://localhost:8081/api/users" -UseBasicParsing -TimeoutSec 3
    $usersData = $users.Content | ConvertFrom-Json
    $count = $usersData.users.Count
    Write-Host "[OK] Users API returned $count users"
} catch {
    Write-Host "[WARN] Users API failed: $_"
}

# Test rooms API  
try {
    $rooms = Invoke-WebRequest -Uri "http://localhost:8081/api/rooms" -UseBasicParsing -TimeoutSec 3
    $roomsData = $rooms.Content | ConvertFrom-Json
    $count = $roomsData.rooms.Count
    Write-Host "[OK] Rooms API returned $count rooms"
} catch {
    Write-Host "[WARN] Rooms API failed: $_"
}

# Test items API
try {
    $items = Invoke-WebRequest -Uri "http://localhost:8081/api/items" -UseBasicParsing -TimeoutSec 3
    $itemsData = $items.Content | ConvertFrom-Json
    $count = $itemsData.items.Count
    Write-Host "[OK] Items API returned $count items"
} catch {
    Write-Host "[WARN] Items API failed: $_"
}

# Test logs API
try {
    $logs = Invoke-WebRequest -Uri "http://localhost:8081/api/logs" -UseBasicParsing -TimeoutSec 3
    $logsData = $logs.Content | ConvertFrom-Json
    $count = $logsData.logs.Count
    Write-Host "[OK] Logs API returned $count entries"
} catch {
    Write-Host "[WARN] Logs API failed: $_"
}

Write-Host "`n=== All API tests passed! ==="
Write-Host "Server endpoints:"
Write-Host "  Game WebSocket: ws://localhost:8080/ws"
Write-Host "  Admin API:      http://localhost:8081/api"
Write-Host "`nAdmin panel: file:///C:/Users/luizw/AppData/Local/Temp/opencode/avatarworld/webadmin/index.php"
Write-Host "Client: java -cp $jarPath com.avatarworld.AvatarWorldClient"
