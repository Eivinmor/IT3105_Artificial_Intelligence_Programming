import gym
import random

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3


def run_algorithm(env):
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
        action = get_random_action(env.action_space.n)
        observation, reward, done, info = env.step(action)
        total_reward += reward

        print("Timestep:", c)
        print_env(env)

    print("Episode finished after {} timesteps".format(c))
    return total_reward


def get_random_action(n):
    return random.randint(0, n-1)


def print_env(env):
    env.render()
    print()


def main():
    env = gym.make('FrozenLake-v0')
    # env = gym.make('Taxi-v1')
    total_reward = run_algorithm(env)
    print("Total reward:", total_reward)


main()
