import gym
import random

LEFT = 0
DOWN = 1
RIGHT = 2
UP = 3
arrows = ['L', 'D', 'R', 'U']


def run_algorithm(env, q_function):
    observation = env.reset()
    x = observation % 4
    y = int(observation / 4)
    done = False
    c = 1

    print("Timestep:", c)
    print("Initial board state:")
    print_state(x, y, env)

    print("Running algorithm:")
    while not done:
        c += 1
        action = get_best_action(q_function)
        observation, reward, done, info = env.step(action)
        x = observation % 4
        y = int(observation / 4)

        print("Timestep:", c)
        print_state(x, y, env)

    print("Episode finished after {} timesteps".format(c))


def get_best_action(q_function):
    best_action = 0
    best_value = 0
    for i in range(len(q_function)):
        if q_function[i] > best_value:
            best_action = i
            best_value = q_function[i]
    return best_action


def generate_random_policy(n):
    policy = []
    for y in range(n):
        policy.append([])
        for x in range(n):
            randint = random.randint(0, n-1)
            policy[y].append(randint)
    return policy


def print_state(x, y, env):
    print("y:", y, "x:", x)
    env.render()
    print()


def print_policy(policy):
    print("Policy:")
    for row in policy:
        for cell in row:
            print("{:^3}".format(arrows[cell]), end="")
        print()
    print()


def print_info(info):
    for key, v in info.items():
        print("Key:", key, "Value:", v)


def main():
    q_function = [0.5, 1, 0.5, 0.5]
    env = gym.make('FrozenLake-v0')
    run_algorithm(env, q_function)


main()
