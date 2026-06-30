Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld"
WshShell.Run "java -jar target\avatarworld-1.0.0.jar", 0, False
