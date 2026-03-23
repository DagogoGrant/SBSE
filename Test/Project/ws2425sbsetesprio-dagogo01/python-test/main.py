
#hello = ("HELLO")
#world = ("WORLD")
#print(hello + " " + world)

l = ["Alpha", "Beta", "Gamma","Delta"]
l.append("Epsilon")
sublist = l[:3]
f = l[3:]
print(sublist)
print(f)

#name = input("WHATS YOUR NAME " )
#color = input("whats your fav color ")
#print(name + " likes "  + color)

#year = int(input("Enter your birth year: "))
# current = int(input("Enter the current year: "))
# age = current - year
# print(type(age))
# print("You are  " + str(age) + " years old.SUCH A KID!!!!!")

# num = int(input(" Enter Number"))
# if num % 2 == 0:
#     print(' Even')
# else:
#     print(' Odd')

# def even_odd(num):
#      if num % 2 == 0:
#          return 'Even'
#      else:
#          return 'Odd'
# num = int(input(" Enter Number"))
# print(even_odd(num))
     
# try:
#     # Prompt the user for hours and rate per hour
#     hours = float(input("Enter hours worked: "))
#     rate_per_hour = float(input("Enter rate per hour: "))
    
#     # Check for overtime
#     if hours > 40:
#         # Calculate overtime pay (1.5 times the rate for hours above 40)
#         overtime_hours = hours - 40
#         gross_pay = (40 * rate_per_hour) + (overtime_hours * rate_per_hour * 1.5)
#     else:
#         # Regular pay if hours are 40 or less
#         gross_pay = hours * rate_per_hour
    
#     # Display the result
#     print("Gross pay: $", gross_pay)

# except ValueError:
#     print("Error: Please enter numeric input.")
# n = 5
# while n > 0 :
#     print(n)
#     break
# print("Finish")
# print(n)

# count = 0
# while True:  # This would normally be an endless loop
#     print("Count is:", count)
#     count += 1
#     if count == 3:  # When count reaches 3, break the loop
#         break

# count = 0  # We start with a number, here it's zero
# while count < 3:  # Keep going as long as count is less than 3
#     print("Count is:", count)  # Print the current count
#     count += 1  # Add 1 to count every time the loop runs

# count = 0
# while count < 3:  # This loop will run exactly 3 times
#     print("Hello")
#     count += 1

# user_input = ""
# while user_input != "stop":
#     user_input = input("Type something (type 'stop' to end): ")

# count = 0
# while count < 5:
#     count += 1
#     if count == 3:  # When count is 3, skip the print statement
#         continue
#     print("Count is:", count)
