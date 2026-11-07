Set WshShell = CreateObject("WScript.Shell")
WshShell.CurrentDirectory = "C:\Users\luizw\AppData\Local\Temp\opencode\avatarworld"
WshShell.Run "java -cp target\avatarworld-1.0.0.jar com.avatarworld.AvatarWorldClient", 1, False
