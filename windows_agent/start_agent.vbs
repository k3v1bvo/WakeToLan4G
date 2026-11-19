Set WshShell = CreateObject("WScript.Shell")
Set fso = CreateObject("Scripting.FileSystemObject")
scriptDir = fso.GetParentFolderName(WScript.ScriptFullName)
WshShell.CurrentDirectory = scriptDir
agentScript = scriptDir & "\pc_agent.py"

WshShell.Run "pythonw """ & agentScript & """", 0, False
