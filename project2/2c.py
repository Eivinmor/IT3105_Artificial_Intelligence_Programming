import gym
import random

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3
direction = ['W', 'S', 'E', 'N']
epsilon = 0.2


def run_algorithm(env, q_function):
    env.reset()
    done = False
    c = 1
    total_reward = 0

    print("Timestep:", c)
    print("Initial board state:")
    print_env(env)

    print("Running algorithm:")
    while not done:
        c += 1
        action = get_epsilon_greedy_action(q_function)
        observation, reward, done, info = env.step(action)
        total_reward += reward

        print("Timestep:", c)
        print_env(env)

    print("Episode finished after {} timesteps".format(c))
    return total_reward


def get_best_action(q_function):
    best_action = 0
    best_value = 0
    for i in range(len(q_function)):
        if q_function[i] > best_value:
            best_action = i
            best_value = q_function[i]
    return best_action


def get_epsilon_greedy_action(q_function):
    random_nr = random.random()
    print("1-epsilon:", 1-epsilon)
    print("Random nr:", random_nr)
    if 1-epsilon > random_nr:
        best_action = get_best_action(q_function)
        print("Performing best action:", direction[best_action])
        return best_action
    random_action = random.randint(0, 3)
    print("Performing random action:", direction[random_action])
    return random_action


def print_env(env):
    env.render()
    print()


def main():
    env = gym.make('FrozenLake-v0')
    q_function = [0.5, 1, 0.5, 0.5]

    total_reward = run_algorithm(env, q_function)
    print("Total reward:", total_reward)


main()
