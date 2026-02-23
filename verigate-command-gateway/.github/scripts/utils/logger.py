import sys
from typing import Optional

class Logger:
    @staticmethod
    def log(message: str, indent: Optional[int] = None) -> None:
        """
        Log a message with optional indentation.
        
        Args:
            message: The message to log
            indent: Number of tabs to indent the message (optional)
        """
        if indent is not None:
            message = '\t' * indent + message
        print(message, flush=True)

    @staticmethod
    def error(message: str, indent: Optional[int] = None) -> None:
        """
        Log an error message with optional indentation.
        
        Args:
            message: The error message to log
            indent: Number of tabs to indent the message (optional)
        """
        if indent is not None:
            message = '\t' * indent + message
        print(f"ERROR: {message}", file=sys.stderr, flush=True) 