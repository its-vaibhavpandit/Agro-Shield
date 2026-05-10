import speech_recognition as sr
import sys

def main():
    culture = "en-US"
    if len(sys.argv) > 1:
        culture = sys.argv[1]
        
    recognizer = sr.Recognizer()
    try:
        with sr.Microphone() as source:
            recognizer.adjust_for_ambient_noise(source, duration=0.5)
            audio = recognizer.listen(source, timeout=5, phrase_time_limit=5)
            text = recognizer.recognize_google(audio, language=culture)
            # Ensure proper encoding for windows terminal output
            sys.stdout.reconfigure(encoding='utf-8')
            print(text)
    except sr.WaitTimeoutError:
        print("NO_SPEECH")
    except sr.UnknownValueError:
        print("NO_SPEECH")
    except sr.RequestError as e:
        print(f"ERROR: {e}")
    except Exception as e:
        print(f"ERROR: {e}")

if __name__ == "__main__":
    main()
