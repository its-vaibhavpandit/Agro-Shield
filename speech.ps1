param([string]$culture = "")

Add-Type -AssemblyName System.Speech

try {
    if ($culture -ne "") {
        $recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine($culture)
    } else {
        $recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine
    }

    $recognizer.SetInputToDefaultAudioDevice()
    $grammar = New-Object System.Speech.Recognition.DictationGrammar
    $recognizer.LoadGrammar($grammar)

    # Console.WriteLine("Listening...") is skipped to just return the text
    $result = $recognizer.Recognize((New-TimeSpan -Seconds 5))

    if ($result) {
        Write-Output $result.Text
    } else {
        Write-Output "NO_SPEECH"
    }
} catch {
    Write-Output "ERROR: $_"
}
