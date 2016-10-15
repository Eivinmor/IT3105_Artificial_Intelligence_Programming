import gym

# WEST = 0
# SOUTH = 1
# EAST = 2
# NORTH = 3


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
        action = get_best_action(q_function)
        observation, reward, done, info = env.step(action)
        total_reward += reward

        print("Timestep:", c)
        print_env(env)

    print("Episode finished after {} timesteps".format(c))
    return total_reward


def get_best_action(q_function):
    return max(q_function)


def print_env(env):
    env.render()
    print()


def main():
    env = gym.make('FrozenLake-v0')
    q_function = [0.5, 1, 0.5, 0.5]

    total_reward = run_algorithm(env, q_function)
    print("Total reward:", total_reward)


main()
