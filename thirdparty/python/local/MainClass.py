
def ask_Input():
    print("Insert number1")
    number1 = input("> ")
    print("Insert number2")
    number2 = input("> ")

    print("Sum: {}".format(int(number1) + int(number2)))

if __name__ == "__main__":
    ask_Input()
